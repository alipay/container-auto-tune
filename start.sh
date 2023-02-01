#!/usr/bin/env bash

# Regular Colors
Red=$'\e[1;31m'
Green=$'\e[1;32m'
Blue=$'\e[1;34m'
end=$'\e[0m'
TMAESTRO_YAML=tmaestro-lite.yaml
TMAESTRO_YAML_NEW=/tmp/tmaestro-lite.yaml.new
TMAESTRO_HOME_PAGE_URL="http://localhost:30081"

if ! [ -x "$(command -v kubectl)" ]; then
  echo "${Red}Error: kubectl is not installed. (kubectl required to install tmaestro, please install kubectl firstly.)${end}"
  exit 1
fi

get_latest_image(){
  docker images -a | grep -E "^tmaestro/" | sort -r | head -1 | awk  '{print $1}'
  if [ $? -ne 0 ]; then
      echo "${Red}No tmaestro images found. please execute make cmd firstly.${end}"
      exit 119
  fi
  image=`docker images -a | grep -E "^tmaestro/" | sort -r | head -1 | awk  '{print $1}'`
  export "TMAESTRO_IMAGE=${image}"
  echo "get TMAESTRO_IMAGE is $TMAESTRO_IMAGE"
}

create_config_map(){
  echo "${Green} ===> start to create configmap to store local config file.${end}"
  kubectl get configmap tmaestro-kube-config
  if [ $? -ne 0 ]; then
    kubeconfig=`ls ~/.kube/config`
    echo "${kubeconfig}"
    cmd=$(echo "kubectl create configmap tmaestro-kube-config --from-file=${kubeconfig}")
    $cmd
  fi
}

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
    print_success_icon
    echo -e "${Blue}Congratulations!!! Twatch is now installed successfully.${end}"
}

print_success_icon(){
  echo "🍺 🍺 🍺 🍺 🍺 🍺"
}

print_tmaestro(){
  echo '  ______    __  ___                         __                ';
  echo ' /_  __/   /  |/  /  ____ _  ___    _____  / /_   _____  ____ ';
  echo '  / /     / /|_/ /  / __ \`/ / _ \  / ___/ / __/  / ___/ / __ \';
  echo ' / /     / /  / /  / /_/ / /  __/ (__  ) / /_   / /    / /_/ /';
  echo '/_/     /_/  /_/   \__,_/  \___/ /____/  \__/  /_/     \____/ ';
  echo '                                                               ';
}

print_tmaestro_success(){
  print_tmaestro
  print_success_icon
  echo -e "${Blue} Congratulations!!! Tmaestro is now installed successfully. please go to TMaestro homepage(${TMAESTRO_HOME_PAGE_URL}) to explore it."
}

install_tmaestro(){
    echo -e "${Green}===> start deploy tmaestro.${end}"
    kubectl get pods | grep "tmaestro" | grep Running
        if [ $? == 0 ]; then
          echo "${Green} update tmaestro image=${image}.${end}"
          kubectl set image deployment/tmaestro tmaestro-server=${image}
          print_tmaestro_success
          exit 119
        fi
    echo -ne "${Green}[ ========>                                                       (10%)]${end}\r"
    sed "s/REPLACE_WITH_TMAESTRO_IMAGE/${image}/"  tmaestro-lite.yaml > /tmp/tmaestro-lite.yaml
    kubectl apply -f /tmp/tmaestro-lite.yaml
    echo -ne "${Green}[ ================================>                               (53%)]${end}\r"
    sleep 6
    kubectl get pods | grep "tmaestro" | grep Running
    if [ $? != 0 ]; then
      echo "${Red}Install tmaestro failed, please contact tmaestro experts to handle it!${end}"
      exit 119
    fi
    echo -ne "${Green}[ ================================================================(100%)]${end}\r"
    echo -ne '\n'
    print_tmaestro_success
}

main(){
  # build image
  echo "${Green} ===> start build image.${end}"
  make
  # get latest image
  get_latest_image
  # create configMap to store local config file
  create_config_map
  # deploy tmaestro-server
  install_tmaestro
  # deploy twatch
  install_demonset
}

main