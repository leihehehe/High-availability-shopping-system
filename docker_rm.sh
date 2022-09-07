#!/usr/bin/env bash
IMAGE_NAME=$1
echo "$IMAGE_NAME"
if [ ! -n "$IMAGE_NAME" ]; then
  echo parameters cannot be empty
  exit 4
fi

echo '================get container id=================='
CID=$(docker ps -aqf "name=$IMAGE_NAME")
echo container id=$CID
if [ -n "$CID" ]; then
  echo the container of $IMAGE_NAME exists, stopping and removing the container.
  docker rm -f $IMAGE_NAME
else
  echo the container of $IMAGE_NAME does not exists.
fi

echo '================get image id=================='
IID=$(docker images | grep "$IMAGE_NAME" | awk '{print $3}')
echo the image id is $IID

if [ -n "$IID" ]; then
  echo $IMAGE_NAME exists
  docker rmi $IID
else
  echo $IMAGE_NAME does not exist
fi


