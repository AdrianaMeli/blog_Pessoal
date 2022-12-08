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

            assertEquals(HttpStatus.NOT_FOUND, corpoResposta.getStatusCode());
        }
    @Test
    @DisplayName("atualizar usuario")
    public void atualizarUsuario() {
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));
        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg");

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);


        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
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

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Adriana Nogueira", "adriana@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));
        ResponseEntity<String> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/1", HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

    }


}


