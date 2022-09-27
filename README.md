Custom Hibernate implementation attempt 

Such annotation implementated: 
@Table
@Column
@OneToMany
@ManyToOne


Such mechanisms implemented
-Session(only with find... operations, also close)
-Cache
-DirtyChecking
-LazyLoading  
  
Todo:
-implement Tranasctions
-implement @Join for OneToOne relation
-implement Persist and Update Delete
-implement Actions and ActionQueue for all operation types
-refactorin
-cover all with tests
