import argparse
import hashlib
import os
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv
from langchain.schema import Document
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from langchain_community.document_loaders import TextLoader, PyMuPDFLoader
from langchain_openai import OpenAIEmbeddings

# ====== cấu hình ======
CHROMA_PATH = "chroma"
EMB_MODEL = "text-embedding-3-small"
DATA_PATHS = ["data/documents", "data/uploads"]  # duyệt cả 2 thư mục


# ====== helpers ======
def sha1_file(path: str) -> str:
    h = hashlib.sha1()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def text_split(docs: list[Document]) -> list[Document]:
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=900, chunk_overlap=120, add_start_index=True
    )
    return splitter.split_documents(docs)


def load_one_file(path: str) -> list[Document]:
    ext = Path(path).suffix.lower()
    if ext == ".pdf":
        docs = PyMuPDFLoader(path).load()  # 1 doc/trang
        return text_split(docs)  # chunk theo ký tự như .md/.txt
    elif ext in (".md", ".txt"):
        docs = TextLoader(path, encoding="utf-8").load()
        return text_split(docs)
    else:
        raise ValueError(f"Unsupported extension: {ext}")


def ensure_db() -> Chroma:
    embeddings = OpenAIEmbeddings(model=EMB_MODEL)
    return Chroma(persist_directory=CHROMA_PATH, embedding_function=embeddings)


def upsert_file(file_path: str):
    assert os.path.exists(file_path), f"File not found: {file_path}"
    source = os.path.normpath(file_path)
    checksum = sha1_file(file_path)

    db = ensure_db()

    # đã có bản y hệt? -> bỏ qua
    same = db.get(where={"source": source, "checksum": checksum})
    if same and same.get("ids"):
        print(f"[SKIP] Up-to-date: {source} ({checksum[:8]}...)")
        return

    # có bản cũ khác checksum? -> xoá trước
    old = db.get(where={"source": source})
    if old and old.get("ids"):
        print(f"[CLEAN] Remove old chunks: {source}")
        db.delete(where={"source": source})

    # chunk và gắn metadata
    chunks = load_one_file(file_path)
    for c in chunks:
        c.metadata.update({
            "source": source,
            "checksum": checksum,
            "ingested_at": datetime.utcnow().isoformat() + "Z",
        })

    print(f"[EMBED] {source} -> {len(chunks)} chunks | model={EMB_MODEL}")
    db.add_documents(chunks)  # Chroma 0.4+ auto-persist
    print(f"[DONE] Upserted: {source}")


def iter_files_under(dir_path: str):
    for ext in ("*.md", "*.txt", "*.pdf"):
        yield from Path(dir_path).rglob(ext)


def upsert_paths(paths: list[str]):
    for p in paths:
        p = os.path.normpath(p)
        if os.path.isdir(p):
            for fp in iter_files_under(p):
                upsert_file(str(fp))
        else:
            upsert_file(p)


def main():
    load_dotenv()
    assert os.getenv("OPENAI_API_KEY"), "Missing OPENAI_API_KEY in .env"

    ap = argparse.ArgumentParser(
        description="Add/Update .md/.pdf into Chroma by checksum (no full rebuild)."
    )
    ap.add_argument(
        "path",
        nargs="?",
        help="File or folder to upsert. If omitted, uses default data folders."
    )
    args = ap.parse_args()

    if args.path:
        upsert_paths([args.path])
    else:
        upsert_paths(DATA_PATHS)


if __name__ == "__main__":
    main()
