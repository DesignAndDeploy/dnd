from dnd/lighttpd
maintainer Philipp Adolf

# needed for the cgi scripts
run apt-get -y install python-simplejson

# install lighttpd config
add 10-cgi.conf /etc/lighttpd/conf-available/10-cgi.conf
add lighttpd.conf /etc/lighttpd/lighttpd.conf
run chown root:root /etc/lighttpd/conf-available/10-cgi.conf /etc/lighttpd/lighttpd.conf
run lighttpd-enable-mod cgi

# add scripts
run rm /var/www/index.lighttpd.html
add cumulus /var/www
run chown -R www-data:www-data /var/www

# make server available on cumulus' port
expose 51525:80
cmd lighttpd -D -f /etc/lighttpd/lighttpd.conf
