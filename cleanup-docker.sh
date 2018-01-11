# Delete all non-running containers
sudo docker ps -aq --no-trunc | sudo xargs docker rm
# Delete all volumes not associated with a container
sudo docker volume rm $(sudo docker volume ls -qf dangling=true)
# Delete all unused images
sudo docker image prune
