FROM php:8.2-apache

RUN apt-get update \
    && apt-get install -y --no-install-recommends libpq-dev ca-certificates \
    && docker-php-ext-install pdo_pgsql \
    && rm -rf /var/lib/apt/lists/*

RUN a2enmod rewrite headers \
    && sed -i 's/Listen 80/Listen 10000/' /etc/apache2/ports.conf \
    && sed -i 's/:80>/:10000>/' /etc/apache2/sites-available/000-default.conf

COPY . /var/www/html/gadget_shop/
COPY db.render.php /var/www/html/gadget_shop/db.php
COPY render-apache.conf /etc/apache2/conf-available/render-security.conf

RUN a2enconf render-security \
    && printf 'display_errors=Off\nlog_errors=On\nexpose_php=Off\ndate.timezone=Asia/Manila\n' > /usr/local/etc/php/conf.d/production.ini

EXPOSE 10000

CMD ["apache2-foreground"]
