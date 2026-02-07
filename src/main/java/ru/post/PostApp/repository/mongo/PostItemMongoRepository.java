package ru.post.PostApp.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.post.PostApp.domain.document.PostItemDocument;

@Repository
public interface PostItemMongoRepository extends MongoRepository<PostItemDocument, String> {
}
