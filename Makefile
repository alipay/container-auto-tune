.PHONY: build
autotune_image=$(shell docker images --filter=reference="*tmaestro*" -q)
version=$(shell date '+%Y%m%d%H%M')
build:
ifeq ($(strip $(autotune_image)),)
	@echo "nope"
else
#	docker rmi -f ${autotune_image}
endif
	mvn -s ./.mvn/settings.xml -DskipTests=true clean package
	docker buildx build --platform linux/amd64  . -t tmastro/${version}
	# image: tmastro/${version}
package:
	mvn -s ./.mvn/settings.xml -DskipTests=true clean package
