const express = require("express");
const morgan = require("morgan");
require("dotenv").config();

const healthRoute = require("./routes/health");

const app = express();

app.use(express.json());
app.use(morgan("combined"));

const PORT = process.env.PORT || 3000;

app.get("/", (req, res) => {
  res.json({
    message: "DevOps CI/CD Demo App Running 🚀",
    environment: process.env.NODE_ENV || "development"
  });
});

app.use("/health", healthRoute);

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});