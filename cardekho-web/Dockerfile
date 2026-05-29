# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

# Runtime: nginx + SPA; proxies /api to host (override in compose for real deployments)
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/cardekho-web/browser /usr/share/nginx/html
EXPOSE 80
