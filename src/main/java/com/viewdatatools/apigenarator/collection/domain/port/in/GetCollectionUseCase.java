package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;

public interface GetCollectionUseCase {
    CollectionDomain getCollection(String username, String collectionName);
}
