#!/usr/bin/env bash

# Regular Colors
Red=$'\e[1;31m'
Green=$'\e[1;32m'
Blue=$'\e[1;34m'
end=$'\e[0m'
TMAESTRO_YAML=tmaestro-lite.yaml
TMAESTRO_YAML_NEW=/tmp/tmaestro-lite.yaml.new

if ! [ -x "$(command -v kubectl)" ]; then
  echo "${Red}Error: kubectl is not installed. (kubectl required to install tmaestro, please install kubectl firstly.)${end}"
  exit 1
fi

check_image(){
  docker images -a | grep -E "^tmaestro/" | sort -r | head -1 | awk  '{print $1}'
  if [ $? -ne 0 ]; then
      echo "${Red}No tmaestro images found. please execute make cmd firstly.${end}"
      exit 119
  fi
  image=`docker images -a | grep -E "^tmaestro/" | sort -r | head -1 | awk  '{print $1}'`
  export "TMAESTRO_IMAGE=${image}"
  echo "get TMAESTRO_IMAGE is $TMAESTRO_IMAGE"
}

tmaestro_yaml_env_subst(){
  echo "get tmaestro_yaml_env_subst TMAESTRO_IMAGE is $TMAESTRO_IMAGE"
  envsubst < ${TMAESTRO_YAML} > "${TMAESTRO_YAML_NEW}"
  cp ${TMAESTRO_YAML_NEW}  ./${TMAESTRO_YAML}
}

echo "${Green} ===> start build image.${end}"
make
check_image
tmaestro_yaml_env_subst

echo "${Green} ===> start to get latest tmaestro image.${end}"
check_image
tmaestro_yaml_env_subst

echo "${Green} ===> start to mount kube config file.${end}"
kubectl get configmap tmaestro-kube-config
if [ $? -ne 0 ]; then
  kubeconfig=`ls ~/.kube/config`
  echo "${kubeconfig}"
  cmd=$(echo "kubectl create configmap tmaestro-kube-config --from-file=${kubeconfig}")
  $cmd
fi

echo "${Green}===> start to create tmaestro-properties configmap${end}"
#kubectl apply -f tmaestro-properties-configmap.yaml

echo "${Green}===> start deploy tmaestro.${end}"
kubectl apply -f tmaestro-lite.yaml

install_demonset(){
    echo -ne "${Green}[ ========>                                                       (10%)]${end}\r"
    kubectl apply -f twatch.yaml >/dev/null 2>&1
    echo -ne "${Green}[ ================================>                               (53%)]${end}\r"
    sleep 3
    kubectl get pods | grep "twatch" | grep Running
    if [ $? != 0 ]; then
      echo "${Red}Install twatch demonSet failed, please contact tmaestro experts to handle it!${end}"
      exit 119
    fi
    echo -ne "${Green}[ ================================================================(100%)]${end}\r"
}

print_success_icon(){
  echo "🍺 🍺 🍺 🍺 🍺 🍺"
}
install_demonset
print_success_icon
echo -e "${Blue}Congratulations!!! Twatch is now installed successfully.${end}"

echo -e "${Green}===> start deploy tmaestro.${end}"
install_tmaestro(){
    echo -ne "${Green}[ ========>                                                       (10%)]${end}\r"
    kubectl apply -f tmaestro-lite.yaml
    echo -ne "${Green}[ ================================>                               (53%)]${end}\r"
    sleep 6
    kubectl get pods | grep "tmaestro" | grep Running
    if [ $? != 0 ]; then
      echo "${Red}Install tmaestro failed, please contact tmaestro experts to handle it!${end}"
      exit 119
    fi
    echo -ne "${Green}[ ================================================================(100%)]${end}\r"
    echo -ne '\n'
}

print_tmaestro(){
  echo '  ______    __  ___                         __                ';
  echo ' /_  __/   /  |/  /  ____ _  ___    _____  / /_   _____  ____ ';
  echo '  / /     / /|_/ /  / __ \`/ / _ \  / ___/ / __/  / ___/ / __ \';
  echo ' / /     / /  / /  / /_/ / /  __/ (__  ) / /_   / /    / /_/ /';
  echo '/_/     /_/  /_/   \__,_/  \___/ /____/  \__/  /_/     \____/ ';
  echo '                                                               ';
}

TMAESTRO_HOME_PAGE_URL="http://localhost:30083"
print_tmaestro_success(){
  print_tmaestro
  print_success_icon
  echo -e "${Blue} Congratulations!!! Tmaestro is now installed successfully. please go to TMaestro homepage(${TMAESTRO_HOME_PAGE_URL}) to explore it."
}

install_tmaestro
print_tmaestro_success