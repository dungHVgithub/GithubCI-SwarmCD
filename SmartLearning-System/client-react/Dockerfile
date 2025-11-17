# ===== BUILD =====
FROM node:18-alpine AS build
WORKDIR /app

# copy file package trước để cache npm ci
COPY package*.json ./
RUN npm ci

# copy source và build
COPY . .
RUN npm run build

# ===== RUN =====
FROM nginx:alpine

# copy build React sang nginx
COPY --from=build /app/build /usr/share/nginx/html

# folder để mount runtime-config (configMap/volume)
RUN mkdir -p /usr/share/nginx/html/configs

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]