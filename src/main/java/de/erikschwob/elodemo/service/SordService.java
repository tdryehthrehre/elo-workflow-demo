package de.erikschwob.elodemo.service;

import de.erikschwob.elodemo.model.*;
import de.erikschwob.elodemo.repository.MaskRepository;
import de.erikschwob.elodemo.repository.SordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
public class SordService {

    private final SordRepository sordRepository;
    private final MaskRepository maskRepository;

    public SordService(SordRepository sordRepository, MaskRepository maskRepository) {
        this.sordRepository = sordRepository;
        this.maskRepository = maskRepository;
    }

    public Sord createDocument(String shortDescription, Long maskId, Long parentId,
                               Map<String, String> fieldValues) {
        Mask mask = maskId != null
            ? maskRepository.findById(maskId).orElseThrow(() ->
                new NoSuchElementException("Mask not found: " + maskId))
            : null;

        Sord sord = new Sord(shortDescription, mask, NodeType.DOCUMENT);

        if (parentId != null) {
            Sord parent = sordRepository.findById(parentId).orElseThrow(() ->
                new NoSuchElementException("Parent Sord not found: " + parentId));
            sord.setParent(parent);
        }

        if (fieldValues != null) {
            fieldValues.forEach(sord::addField);
        }

        return sordRepository.save(sord);
    }

    public Sord createFolder(String name, Long parentId) {
        Sord folder = new Sord(name, null, NodeType.FOLDER);
        if (parentId != null) {
            Sord parent = sordRepository.findById(parentId).orElseThrow(() ->
                new NoSuchElementException("Parent not found: " + parentId));
            folder.setParent(parent);
        }
        return sordRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public Sord getById(Long id) {
        return sordRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Sord not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Sord> getRootNodes() {
        return sordRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Sord> getChildren(Long parentId) {
        return sordRepository.findByParentId(parentId);
    }

    @Transactional(readOnly = true)
    public List<Sord> search(String term) {
        return sordRepository.searchByDescription(term);
    }
}
