package com.generation.blogpessoal.controller;


import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start(){

        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Root", "root@root.com", "rootroot", " "));

    }

    @Test
    @DisplayName("Cadastrar Um Usuário")
    public void deveCriarUmUsuario() {

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                "Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

    }
        @Test
        @DisplayName("Não deve permitir duplicação do Usuário")
        public void naoDeveDuplicarUsuario() {

            usuarioService.cadastrarUsuario(new Usuario(0L,
                    "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

            HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                    "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

            ResponseEntity<Usuario> corpoResposta = testRestTemplate
                    .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

            assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
        }
    @Test
    @DisplayName("Atualizar um Usuário")
    public void deveAtualizarUmUsuario() {

        /**
         * Persiste um objeto da Classe Usuario no Banco de dados através do Objeto da Classe UsuarioService e
         * guarda o objeto persistido no Banco de Dadoas no Objeto usuarioCadastrado, que será reutilizado abaixo.
         *
         * O Objeto usuarioCadastrado será do tipo Optional porquê caso o usuário não seja persistido no Banco
         * de dados, o Optional evitará o erro NullPointerException (Objeto Nulo).
         */
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg"));
        /**
         *  Cria um Objeto da Classe Usuário contendo os dados do Objeto usuarioCadastrado, que foi persistido na
         *  linha anterior, alterando os Atributos Nome e Usuário (Atualização dos Atributos).
         *
         * Observe que para obter o Id de forma automática, foi utilizado o método getId() do Objeto usuarioCadastrado.
         */
        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "https://i.imgur.com/yDRVeK7.jpg");

        /**
         * Insere o objeto da Classe Usuario (usuarioUpdate) dentro de um Objeto da Classe HttpEntity (Entidade HTTP)
         */
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        /**
         * Cria um Objeto da Classe ResponseEntity (corpoResposta), que receberá a Resposta da Requisição que será
         * enviada pelo Objeto da Classe TestRestTemplate.
         *
         * Na requisição HTTP será enviada a URL do recurso (/usuarios/atualizar), o verbo (PUT), a entidade
         * HTTP criada acima (corpoRequisicao) e a Classe de retornos da Resposta (Usuario).
         *
         * Observe que o Método Atualizar não está liberado de autenticação (Login do usuário), por isso utilizamos o
         * Método withBasicAuth para autenticar o usuário em memória, criado na BasicSecurityConfig.
         *
         * Usuário: root
         * Senha: root
         */
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

        /**
         *  Verifica se a requisição retornou o Status Code OK (200)
         * Se for verdadeira, o teste passa, se não, o teste falha.
         */
        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

        /**
         * Verifica se o Atributo Nome do Objeto da Classe Usuario retornado no Corpo da Requisição
         * é igual ao Atributo Nome do Objeto da Classe Usuario Retornado no Corpo da Resposta
         * Se for verdadeiro, o teste passa, senão o teste falha.
         */
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());

        /**
         * Verifica se o Atributo Usuario do Objeto da Classe Usuario retornado no Corpo da Requisição
         * é igual ao Atributo Usuario do Objeto da Classe Usuario Retornado no Corpo da Resposta
         * Se for verdadeiro, o teste passa, senão o teste falha.
         */
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
    }
    @Test
    @DisplayName("listar todos os Usuarios")
    public void deveMostrarTodososUsuarios(){

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Adriana Nogueira", "adriana@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));
        ResponseEntity<String> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);
                assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
    }



    @Test
    @DisplayName("logar o usuario")
    public void logarUsuario() {

        HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<>(
                new UsuarioLogin("root@root.com", "rootroot"));
        ResponseEntity<Usuario> response = testRestTemplate
                .exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        // faz uma comparação de criar um usuário, esperando nos dois objetos através da vírgula passado a mesma resposta.
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    @DisplayName("listar todos os Usuarios por ID")
    public void listarUsuariosId(){
Optional<Usuario>novoUsuario =
        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Adriana Nogueira", "adriana@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/"+novoUsuario.get().getId(), HttpMethod.GET, null, Usuario.class);
        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

    }


}


