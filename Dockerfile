# syntax=docker/dockerfile:1
# Computes the 3-character entropy of WarAndPeace.txt with 4 worker processes once, prints it and exits.
FROM python:3.12-slim
WORKDIR /app
COPY entropy.py WarAndPeace.txt ./
CMD ["python", "entropy.py"]
