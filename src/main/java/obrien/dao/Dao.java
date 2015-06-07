package obrien.dao;

import java.util.List;

/**
 * Minimum contract for a DAO.
 */
public interface Dao<T> {

    public void insert(T element);

    public List<T> retrieveAll(int UserId) ;

}
