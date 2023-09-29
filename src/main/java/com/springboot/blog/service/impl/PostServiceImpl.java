package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;

    private ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.mapper= mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post= mapToEntity(postDto);
        Post newPost = postRepository.save(post);

        PostDto postResponse = mapPostDto(newPost);

        return postResponse;

    }
    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize,String sortBy,String sortDir){

        Sort sort= sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize, sort);

       Page<Post> posts=postRepository.findAll(pageable);

       List<Post> listOfPost= posts.getContent();
       List<PostDto> content= listOfPost.stream().map(post -> mapPostDto(post)).collect(Collectors.toList());

       PostResponse postResponse=new PostResponse();
       postResponse.setContent(content);
       postResponse.setPageNo(posts.getNumber());
       postResponse.setPageSize(posts.getSize());
       postResponse.setTotalElements(posts.getTotalElements());
       postResponse.setTotalPages(posts.getTotalPages());
       postResponse.setLast(posts.isLast());

        return postResponse;

    }

    @Override
    public PostDto getPostById(Long id){
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        return mapPostDto(post);

    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        post.setDescription(postDto.getDescription());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        Post updatedPost= postRepository.save(post);
        return mapPostDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }


    //convert entity to DTO
    private PostDto mapPostDto(Post post){
        PostDto postDto= mapper.map(post, PostDto.class);
        return postDto;
    }

    private Post mapToEntity(PostDto postDto){
        Post post=mapper.map(postDto,Post.class);
        return post;
    }
}
