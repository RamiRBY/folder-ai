# Stage 1: Build the Vue.js application
# We use a specific Node version on Alpine Linux for a small base image.
# The 'as build-stage' names this stage so we can refer to it later.
FROM node:20-alpine as build-stage

# Set the working directory inside the container.
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock, etc.)
# This is done separately to leverage Docker's layer caching.
# If these files don't change, Docker won't re-run 'npm install'.
COPY package*.json ./

# Install project dependencies.
RUN npm install

# Copy the rest of the application's source code.
COPY . .

# Build the application for production.
# This command runs the 'build' script from your package.json.
#RUN npm run build
# Build the application for production.
# MODIFIED: Changed from 'npm run build' to 'npm run build-only' to bypass
# the type-checking step that was causing the build to fail.
# NOTE: The best practice is to fix the TypeScript errors in your code.
# This change is a workaround to allow the image to build for development.
RUN npm run build


# ---

# Stage 2: Serve the application from a lightweight NGINX server
# We use a new, clean base image for the final container.
FROM nginx:stable-alpine as production-stage

# Copy the built static files from the 'build-stage' into NGINX's web root directory.
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copy the custom NGINX configuration file.
# This will overwrite the default NGINX config with our reverse proxy setup.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Tell Docker that the container listens on port 80 at runtime.
EXPOSE 80

# The default command for the nginx image is to start the server.
# This command ensures NGINX stays in the foreground so the container keeps running.
CMD ["nginx", "-g", "daemon off;"]
