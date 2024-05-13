podman build -t registry-bot -f Dockerfile.bot --no-cache .
podman build -t registry-web -f Dockerfile.web --no-cache .