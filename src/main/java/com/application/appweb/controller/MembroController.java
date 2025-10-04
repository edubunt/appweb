package com.application.appweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.appweb.model.Membro;
import com.application.appweb.service.MembroService;

@RestController
@RequestMapping("/membros")
@CrossOrigin(origins = "/*")
public class MembroController {

    @Autowired
    private MembroService membroService;

    // buscar todos os membros
    @GetMapping
    public ResponseEntity<List<Membro>> getAllMembros(){
        List<Membro> membro = membroService.getAllMembros();
        return new ResponseEntity<>(membro,HttpStatus.OK);
    }
    //budcar pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Membro> findMembroById(@PathVariable Long id){
        Membro membro = membroService.findMembroById(id);
        return new ResponseEntity<>(membro,HttpStatus.OK);
    }
    //Salvar membros
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @PostMapping
    public ResponseEntity<Membro> createMempros(@RequestBody Membro menbro){
        Membro novMembro = membroService.creatMembro(menbro);
        return new ResponseEntity<>(novMembro,HttpStatus.CREATED);
    }
    //Atualizar
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Membro> updateMembros(@PathVariable Long id,@RequestBody Membro membroDetails){
        Membro membro  = membroService.updateMembro(id, membroDetails);
        return new ResponseEntity<>(membro,HttpStatus.OK);
    }
    // Deletar
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembros(@PathVariable Long id){
        membroService.deleteMembro(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    //Buscar por intevalos
    @GetMapping("/idade")
    public ResponseEntity<List<Membro>> getMembros(@RequestParam("idadeMinima") int idadeMinima,
                                                   @RequestParam("idadeMaxima") int idadeMaxima){
        List<Membro> membros = membroService.getMembrosByIdade(idadeMinima, idadeMaxima);
        return new ResponseEntity<>(membros,HttpStatus.OK);
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<Membro>> getMembrosByNome(@RequestParam String nome){
        List<Membro> membros = membroService.getMembrosByNome(nome);
        return ResponseEntity.ok(membros);
    }
}

