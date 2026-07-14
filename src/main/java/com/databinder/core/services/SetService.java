package com.databinder.core.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.databinder.core.dto.SetResponse;
import com.databinder.core.dto.request.SetCreateRequest;
import com.databinder.core.entities.CardSet;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.repositories.SetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetService {

    private final SetRepository setRepository;

    public SetResponse create(SetCreateRequest request) {
        CardSet cardSet = new CardSet();
        cardSet.setName(request.getName());
        cardSet.setCode(request.getCode());

        return ResponseMapper.toResponse(setRepository.save(cardSet));
    }

    public SetResponse getById(Long id) {
        CardSet cardSet = setRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("CardSet not found: " + id));

        return ResponseMapper.toResponse(cardSet);
    }

    public List<SetResponse> getAll() {
        return setRepository.findAll()
                .stream()
                .map(ResponseMapper::toResponse)
                .toList();
    }

    public SetResponse update(Long id, SetCreateRequest request) {
        CardSet cardSet = setRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CardSet not found"));

        cardSet.setName(request.getName());
        cardSet.setCode(request.getCode());

        return ResponseMapper.toResponse(setRepository.save(cardSet));
    }

    public void delete(Long id) {
        setRepository.deleteById(id);
    }


}