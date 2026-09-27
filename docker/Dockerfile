FROM node:22-alpine

WORKDIR /app

COPY app/package*.json ./

RUN npm install --omit=dev

COPY app .

EXPOSE 3000

USER node

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD wget -qO- http://localhost:3000/health || exit 1

CMD ["npm", "start"]