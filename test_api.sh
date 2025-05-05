#!/bin/bash

BASE_URL="http://localhost:8080"

echo "1. Creating a new board"
curl -X POST $BASE_URL/boards \
-H "Content-Type: application/json" \
-d '{"title":"First Board","content":"Welcome to the board!","author":"Alice"}'
echo -e "\n"

echo "2. Getting all boards"
curl -X GET $BASE_URL/boards
echo -e "\n"

echo "3. Getting board by ID (ID: 1)"
curl -X GET $BASE_URL/boards/1
echo -e "\n"

echo "4. Updating board (ID: 1)"
curl -X PUT $BASE_URL/boards/1 \
-H "Content-Type: application/json" \
-d '{"title":"Updated Board","content":"Updated content","author":"Alice"}'
echo -e "\n"

echo "5. Deleting board (ID: 1)"
curl -X DELETE $BASE_URL/boards/1
echo -e "\n"