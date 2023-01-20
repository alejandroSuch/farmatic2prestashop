FROM mcr.microsoft.com/mssql/server:2017-latest
ENV ACCEPT_EULA Y
ENV SA_PASSWORD AdministradoR001
ENV MSSQL_SA_PASSWORD AdministradoR001

COPY ./entrypoint.sh /usr/src/app/entrypoint.sh
COPY ./import-data.sh /usr/src/app/import-data.sh
RUN chmod +x /usr/src/app/import-data.sh
COPY ./setup.sql /usr/src/app/setup.sql

# RUN wget "http://security.debian.org/debian-security/pool/updates/main/o/openssl/libssl1.0.0_1.0.1t-1+deb8u7_amd64.deb"
# RUN apt install -y  --allow-downgrades ./libssl1.0.0_1.0.1t-1+deb8u7_amd64.deb

WORKDIR /usr/src/app/
CMD /bin/bash ./entrypoint.sh
