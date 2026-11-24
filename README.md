# Sistema de Gerenciamento de Clínica

## Sobre o projeto

O Sistema de Gerenciamento de Clínica é uma aplicação desktop desenvolvida em Java, utilizando JavaFX para a interface gráfica e MySQL para o armazenamento dos dados.

O sistema tem como objetivo organizar o atendimento de pacientes, controlar filas de espera e facilitar o gerenciamento de médicos e consultas.

## Funcionalidades

- Login com diferentes tipos de usuário.
- Cadastro, consulta e exclusão de médicos.
- Cadastro de pacientes.
- Emissão de senhas normais e prioritárias.
- Organização da fila de atendimento.
- Chamada do próximo paciente.
- Finalização de atendimentos.
- Consulta ao histórico de atendimentos.
- Armazenamento dos dados no MySQL.

## Tipos de usuário

- **Administrador:** gerencia os médicos cadastrados.
- **Médico:** visualiza a fila, chama pacientes e finaliza atendimentos.
- **Paciente:** realiza seu cadastro e emite uma senha para atendimento.

## Especialidades

- Clínica Geral
- Pediatria
- Ginecologia
- Cardiologia
- Psiquiatria

## Prioridades de atendimento

O sistema organiza os atendimentos considerando as categorias prioritárias cadastradas, respeitando a ordem de prioridade implementada no projeto.

## Tecnologias utilizadas

- Java
- JavaFX
- MySQL
- JDBC
- Maven
- IntelliJ IDEA

## Banco de dados

O projeto utiliza o banco de dados MySQL chamado `sistema_clinica`.

Para executar o sistema, é necessário criar o banco de dados e configurar a conexão no arquivo `ConexaoMySQL.java`.

**Importante:** não publique senhas reais do banco de dados no GitHub.

## Como executar o projeto

1. Instale o JDK compatível com o projeto.
2. Instale o MySQL Server.
3. Crie o banco de dados `sistema_clinica`.
4. Configure as credenciais de conexão no projeto.
5. Abra o projeto no IntelliJ IDEA.
6. Aguarde o Maven carregar as dependências.
7. Execute a classe `LoginView`.

## Estrutura do projeto

- `model`: classes que representam os dados do sistema.
- `service`: regras de negócio, conexão e persistência.
- `view`: telas e interface gráfica.

## Autor

Desenvolvido por Eduardo Gontijo.