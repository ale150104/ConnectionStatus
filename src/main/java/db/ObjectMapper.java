package db;

public interface ObjectMapper<T>
{
    String map(T object) throws Exception;
}
