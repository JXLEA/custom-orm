### Low-level JPA implementation 

Implementation:
```
  @Id
  @Table
  @Column
  @OneToMany
  @ManyToOne
```  
  1. Session(only with find... operations, also close)
  2. Cache
  3. DirtyChecking
  3. LazyLoading  
  
> Todo:
>  - implement Transaction
>  - implement @Join for OneToOne relation
>  - implement Persist, Update, Delete
>  - implement Actions and ActionQueue for all operation types
>  1. refactoring
>  2. tests coverage
