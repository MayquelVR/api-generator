package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;

import java.util.List;

public interface ListCollectionsUseCase {
    List<CollectionDomain> listCollections(String username);
}

