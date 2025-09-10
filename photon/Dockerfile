# Use an official Node.js 20 image
FROM node:20-alpine AS base

# Set working directory
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose the port Next.js runs on
EXPOSE 3000

# Command to start the development server
CMD ["npm", "run", "dev"]

