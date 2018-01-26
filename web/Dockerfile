FROM node:8.7.0 as buildArtifacts
COPY . .
RUN yarn && yarn build

FROM nginx:1.11.12
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=buildArtifacts build/ /srv/www/web
CMD ["nginx", "-g", "daemon off;"]