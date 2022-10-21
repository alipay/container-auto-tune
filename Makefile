.PHONY: build
image=$(shell docker images --filter=reference="*tmaestro*" -q)
version=$(shell date '+%Y%m%d%H%M')
build:
ifeq ($(strip $(image)),)
	@echo "nope"
else
#	docker rmi -f ${image}
endif
	mvn -s ./.mvn/settings.xml -DskipTests=true clean package
	docker buildx build --platform linux/amd64  . -t tmaestro/${version}
package:
	mvn -s ./.mvn/settings.xml -DskipTests=true clean package