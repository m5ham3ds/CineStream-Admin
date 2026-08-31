#!/bin/bash
API_KEY="AIzaSyC9CvaM9Mw3NiD-KsOiYvHZHj6XZJQJnPs"
EMAIL="testadmin$(date +%s)@example.com"
PASSWORD="Password123!"

echo "1. Signing up..."
SIGNUP_RES=$(curl -s -X POST "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"returnSecureToken\":true}")

echo $SIGNUP_RES | grep -q "idToken"
if [ $? -eq 0 ]; then
  echo "Signup SUCCESS"
else
  echo "Signup FAILED:"
  echo $SIGNUP_RES
  exit 1
fi

echo "2. Logging in..."
LOGIN_RES=$(curl -s -X POST "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"returnSecureToken\":true}")

echo $LOGIN_RES | grep -q "idToken"
if [ $? -eq 0 ]; then
  echo "Login SUCCESS"
else
  echo "Login FAILED:"
  echo $LOGIN_RES
  exit 1
fi
