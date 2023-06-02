### Low level Hibernate implementation 

Implementated:
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
>  -implement Transaction
>  -implement @Join for OneToOne relation
>  -implement Persist and Update Delete
>  -implement Actions and ActionQueue for all operation types
>  -refactorin
>  -cover all with tests
