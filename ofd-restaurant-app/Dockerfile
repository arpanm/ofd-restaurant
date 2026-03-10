# Build stage
FROM node:20-alpine AS builder
WORKDIR /app

ARG VITE_API_GATEWAY_URL=http://localhost:8085/api/v1
ARG VITE_WS_HOST=localhost:8085
ARG VITE_ORDER_WS_URL=ws://localhost:8084/ws
ENV VITE_API_GATEWAY_URL=$VITE_API_GATEWAY_URL
ENV VITE_WS_HOST=$VITE_WS_HOST
ENV VITE_ORDER_WS_URL=$VITE_ORDER_WS_URL

COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

# Serve with nginx
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]