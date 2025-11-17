# from langchain.document_loaders import DirectoryLoader
import os
import shutil
from pathlib import Path

from dotenv import load_dotenv
from langchain.schema import Document
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import DirectoryLoader, PyMuPDFLoader, TextLoader
from langchain_community.vectorstores import Chroma
# from langchain.embeddings import OpenAIEmbeddings
from langchain_openai import OpenAIEmbeddings

load_dotenv()
# ---- Set OpenAI API key
# your .env file.
DATA_PATHS = ["data/documents", "data/uploads"]
CHROMA_PATH = "chroma"
EMB_MODEL = "text-embedding-3-small"


def main():
    generate_data_store()


def generate_data_store():
    documents = load_documents()
    chunks = split_text(documents)
    save_to_chroma(chunks)


def load_documents():
    docs = []
    for base in DATA_PATHS:
        base = Path(base)
        if not base.exists():
            continue
        # PDF bằng PyMuPDF (mạnh, nhanh)
        pdf_loader = DirectoryLoader(
            str(base),
            glob="**/*.pdf",
            loader_cls=PyMuPDFLoader
        )
        docs += pdf_loader.load()

        # Markdown/Text
        md_loader = DirectoryLoader(
            str(base),
            glob="**/*.md",
            loader_cls=lambda p: TextLoader(p, encoding="utf-8")
        )
        docs += md_loader.load()
    return docs


def split_text(documents: list[Document]):
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=900,  # tăng để giảm số lần gọi API
        chunk_overlap=120,
        add_start_index=True,
        length_function=len,
    )
    chunks = splitter.split_documents(documents)
    print(f"Split {len(documents)} documents into {len(chunks)} chunks.")
    return chunks


def save_to_chroma(chunks: list[Document]):
    # xóa index cũ nếu bạn muốn rebuild full
    if os.path.exists(CHROMA_PATH):
        shutil.rmtree(CHROMA_PATH)

    db = Chroma.from_documents(
        chunks,
        OpenAIEmbeddings(model=EMB_MODEL),
        persist_directory=CHROMA_PATH
    )
    print(f"Chroma collections: {db._collection.count()} documents stored.")
    print(f"Saved {len(chunks)} chunks to {CHROMA_PATH}.")


if __name__ == "__main__":
    main()
