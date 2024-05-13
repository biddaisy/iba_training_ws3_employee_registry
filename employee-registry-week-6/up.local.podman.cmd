podman run ^
 --detach ^
 --env-file=common.env ^
 --expose=8080 ^
 --network=host ^
 --name=registry-web-container registry-web


podman run ^
 --detach ^
 --env-file=common.env ^
 --env-file=bot.env ^
 --env BOT_WEB_BACKEND_URL="http://localhost:8080/registry" ^
 --network=host ^
 --name=registry-bot-container registry-bot
