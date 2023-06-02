###Low level Hibernate implementation 

Implementated:
  *@Id
  *@Table
  *@Column
  *@OneToMany
  *@ManyToOne
  
  Session(only with find... operations, also close)
  -Cache
  -DirtyChecking
  -LazyLoading  
  
> Todo:
>  -implement Transaction
>  -implement @Join for OneToOne relation
>  -implement Persist and Update Delete
>  -implement Actions and ActionQueue for all operation types
>  -refactorin
>  -cover all with tests
