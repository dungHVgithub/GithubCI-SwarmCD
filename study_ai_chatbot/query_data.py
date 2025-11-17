import argparse
import os

from dotenv import load_dotenv
from langchain.prompts import ChatPromptTemplate
from langchain_chroma import Chroma  # ✅ mới
from langchain_openai import OpenAIEmbeddings, ChatOpenAI

CHROMA_PATH = "chroma"
PROMPT_TEMPLATE = """
Answer the question based only on the following context:

{context}

---

Answer the question based on the above context: {question}
"""


def main():
    load_dotenv()
    assert os.getenv("OPENAI_API_KEY"), "Missing OPENAI_API_KEY"

    parser = argparse.ArgumentParser()
    parser.add_argument("query_text", type=str)
    args = parser.parse_args()
    query_text = args.query_text

    embedding_function = OpenAIEmbeddings(model="text-embedding-3-small")

    db = Chroma(persist_directory=CHROMA_PATH,
                embedding_function=embedding_function)

    # Có thể bỏ threshold cứng 0.7 (vì scale khác nhau theo backend)
    docs_scores = db.similarity_search_with_relevance_scores(query_text, k=3)

    if not docs_scores:
        print("Unable to find matching results.")
        return

    context_text = "\n\n---\n\n".join([d.page_content for d, _s in docs_scores])

    prompt = ChatPromptTemplate.from_template(PROMPT_TEMPLATE) \
        .format(context=context_text, question=query_text)

    llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.2)
    answer = llm.invoke(prompt)

    sources = [d.metadata.get("source") for d, _s in docs_scores]
    print(f"Response: {answer}\nSources: {sources}")


if __name__ == "__main__":
    main()
