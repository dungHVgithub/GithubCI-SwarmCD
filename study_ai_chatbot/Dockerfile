FROM python:3.11-slim

# Dùng UTF-8 & tắt .pyc
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

# Cài tool hệ thống cần cho build (pymupdf, chromadb, ...)
RUN apt-get update && apt-get install -y --no-install-recommends \
      build-essential curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Cài dependencies trước (cache layer)
COPY requirements.txt .
RUN python -m pip install -U pip \
 && pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY . .

# Expose port 5000
EXPOSE 5000

# Tạo thư mục persist
RUN mkdir -p /app/chroma /app/data/uploads

# Chạy gunicorn lắng trên 0.0.0.0:5000
CMD ["gunicorn", "-w", "2", "-k", "gthread", "-b", "0.0.0.0:5000", "app:app"]