FROM node:22-alpine AS build-stage
WORKDIR /app

RUN npm install -g @angular/cli

COPY package*.json ./
RUN npm install

COPY . .


RUN sed -i 's/"prerender": true/"prerender": false/g' angular.json && \
    sed -i 's/"ssr": true/"ssr": false/g' angular.json && \
    sed -i 's/"outputMode": "server"/"outputMode": "static"/g' angular.json

RUN npx ng build --configuration production --output-path=/app/build_questoes --prerender false --ssr false

FROM nginx:stable-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage /app/build_questoes/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]