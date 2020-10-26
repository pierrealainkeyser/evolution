# -*- mode: ruby -*-
# vi: set ft=ruby :

$compose_version = "1.24.1"

Vagrant.configure("2") do |config|
  config.vagrant.plugins = ["vagrant-vbguest", "vagrant-docker-compose"]

  config.vm.box = "ubuntu/bionic64"
  config.vbguest.auto_update = false

  config.vm.provider "virtualbox" do |vb|
    vb.memory = 1024
    vb.cpus = 2
    vb.name = "evolution"
    vb.linked_clone = true
  end
  
  # http/https
  config.vm.network "forwarded_port", guest: 80, host: 80
  config.vm.network "forwarded_port", guest: 443, host: 443

  # Empty docker & docker-compose provisioners trick to trigger proxyconf and install them on all VMs
  config.vm.provision :docker
 
  # Issue: https://github.com/leighmcculloch/vagrant-docker-compose/issues/57
  # Segmentation fault when installing docker-compose
  # config.vm.provision :docker_compose, compose_version: $compose_version

  # Manual docker-compose installation fallback
  config.vm.provision "shell", inline: <<-SHELL
    if ! which docker-compose ; then
      echo Installing docker-compose onto machine...
      curl --retry-connrefused --retry 5 -sSL -o /usr/local/bin/docker-compose-#{$compose_version} https://github.com/docker/compose/releases/download/#{$compose_version}/docker-compose-Linux-x86_64
      chmod +x /usr/local/bin/docker-compose-#{$compose_version}
      ln -s /usr/local/bin/docker-compose-#{$compose_version} /usr/local/bin/docker-compose
    fi
  SHELL
  
  config.vm.provision "shell", inline: <<-SHELL
    /vagrant/package.sh
  SHELL
  
  config.vm.provision :docker_compose, compose_version: $compose_version, yml: "/vagrant/docker-compose.yml", run: "always"
end
