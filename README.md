# projeto_tcc_java

Procedimentos para executar o projeto:

- Para utilizar o Google Maps:
  - Coloque a sua chave do Google Maps obtida pelo cadastro do site https://developers.google.com/maps na constante "API_KEY" da classe "CoordenadasApi".
    - Esta classe está localizada no caminho: /src/main/java/com/example/demo/api/CoordenadasApi.java

- Para utilizar o resurso "Esqueci minha senha":
  - Insira um e-mail e senha nas seguintes linhas do arquivo application.properties:
    - spring.mail.username=
    - spring.mail.password=
  - Neste caso está parametrizado para uso de um e-mail @gmail.com
  - Caso uilize outro serviço de e-mail, pode ser necessária alteração na seguinte linha:
    - spring.mail.host=smtp.gmail.com
  - O arquivo application.properties está localizado em:
    - /src/main/resources/application.properties


- Ao iniciar a aplicação pela primeira vez, é incluído no banco de dados um usuário do tipo administrador com os seguintes dados de acesso:
  - Login: admin@sistema.com.br
  - Senha: Q1w2e3r4t5y6
- Esta configuração da inclusão de um usuário administrador está localizada na classe: /src/main/java/com/example/demo/controller/CadastraUsuarioAdministrador.java
- Este login admnistrador é responsável por aprovar/rejeitar as atividades dos usuários, além de ter acesso a logs do sistema.
- Somente o administrador pode cadastrar outros usuários administradores.
