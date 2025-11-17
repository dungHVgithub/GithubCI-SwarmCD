import hashlib
import os
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv
from flask import Flask, request, jsonify
from flask_cors import CORS
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from langchain_community.document_loaders import TextLoader, PyMuPDFLoader
from langchain_openai import OpenAIEmbeddings, ChatOpenAI

load_dotenv()

CHROMA_PATH = "chroma"
EMB_MODEL = os.getenv("EMB_MODEL", "text-embedding-3-small")
app = Flask(__name__)
CORS(
    app,
    resources = {
    r"/query": {
        "origins": ["https://ssfe.hoangvandung.click"]
    },
    r"/upsert": {
        "origins": ["https://ssfe.hoangvandung.click"]
    },
    r"/health": {
        "origins": ["*"]
    },
}
    supports_credentials=False,  # để True nếu thực sự dùng cookie
    allow_headers=["Content-Type", "Authorization"],
    methods=["GET", "POST", "OPTIONS"],
    max_age=86400, )


# ===== Helpers =====
def sha1_file(path: str) -> str:
    h = hashlib.sha1()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def get_db():
    embeddings = OpenAIEmbeddings(model=EMB_MODEL)
    return Chroma(persist_directory=CHROMA_PATH, embedding_function=embeddings)


def split_docs(docs):
    splitter = RecursiveCharacterTextSplitter(chunk_size=900, chunk_overlap=120, add_start_index=True)
    return splitter.split_documents(docs)


# ===== Routes =====
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


@app.route("/upsert", methods=["POST"])
def upsert():
    if "file" not in request.files:
        return jsonify({"error": "No file uploaded"}), 400
    file = request.files["file"]

    save_path = Path("data/uploads") / file.filename
    save_path.parent.mkdir(parents=True, exist_ok=True)
    file.save(save_path)

    ext = save_path.suffix.lower()
    if ext == ".pdf":
        loader = PyMuPDFLoader(str(save_path))
    else:
        loader = TextLoader(str(save_path), encoding="utf-8")

    docs = loader.load()
    chunks = split_docs(docs)

    checksum = sha1_file(str(save_path))
    for c in chunks:
        c.metadata.update({
            "source": str(save_path),
            "checksum": checksum,
            "ingested_at": datetime.utcnow().isoformat() + "Z",
        })

    db = get_db()
    # Xóa cũ nếu khác checksum
    existing = db.get(where={"source": str(save_path)})
    if existing and existing.get("ids"):
        db.delete(where={"source": str(save_path)})
    db.add_documents(chunks)

    return jsonify({"status": "upserted", "chunks": len(chunks), "source": str(save_path)})


@app.route("/query", methods=["POST"])
def query():
    data = request.get_json()
    question = data.get("question")
    if not question:
        return jsonify({"error": "Missing question"}), 400

    top_k = data.get("top_k", 3)
    source_filter = data.get("source_filter")

    db = get_db()
    if source_filter:
        results = db.similarity_search(question, k=top_k, filter={"source": {"$contains": source_filter}})
    else:
        results = db.similarity_search(question, k=top_k)

    if not results:
        return jsonify({"answer": "No matching results", "sources": []})

    context = "\n\n---\n\n".join([d.page_content for d in results])
    llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
    prompt = f"Context:\n{context}\n\nQuestion: {question}\nAnswer:"
    answer = llm.predict(prompt)

    return jsonify({
        "answer": answer,
        "sources": [d.metadata for d in results]
    })


# Run server
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
