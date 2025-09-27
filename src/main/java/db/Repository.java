package db;

import java.util.LinkedList;

public interface Repository<T, K> {

    public T getSingleDataSet(K identifier);

    public LinkedList<T> getAllDataSets();

    public boolean add(T dataSet);

    public boolean delete(K identifier);
}
