package es.unex.prototipoasee.repository;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.room.UserDAO;
import es.unex.prototipoasee.support.AppExecutors;

public class UsersRepository {

    private static final String LOG_TAG = UsersRepository.class.getSimpleName();

    // For Singleton instantiation
    private static UsersRepository sInstance;

    private final UserDAO userDAO;

    private final AppExecutors mExecutors = AppExecutors.getInstance();

    // Mapas para guardar los usuarios almacenados en la BD para el chequeo de credenciales
    private HashMap<String, User> usersInDBByUsername = new HashMap<>();
    private HashMap<String, User> usersInDBByEmail = new HashMap<>();

    private UsersRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
        // Se obtienen los usuarios dados de alta en el sistema
        // Siempre deben obtenerse los usuarios actuales cuando el repositorio es instanciado
        getAllUsers();
    }

    public synchronized static UsersRepository getInstance(UserDAO userDAO) {
        Log.d(LOG_TAG, "Obteniendo el Repositorio de Usuarios");
        if (sInstance == null) {
            sInstance = new UsersRepository(userDAO);
            Log.d(LOG_TAG, "Se ha creado una nueva instancia del Repositorio de Usuarios");
        }
        return sInstance;
    }

    /**
     * Operaciones relacionadas con la gestión de usuarios.
     * En la instanciación del repositorio se obtienen los usuarios ya registrados.
     * Estos usuarios se vuelcan en dos HashMaps para realizar las tareas de consulta de forma mucho más eficiente.
     * Las tareas de inserción (dar de alta a un nuevo usuario), se realizan sobre los HashMaps y la BD para mantener los datos actualizados para las consultas.
     * Las tareas de borrado (eliminar un usuario) s realizan sobre los HashMaps y la BD por la misma razón.
     */

    /**
     * Recupera todos los usuarios almacenados en la BD y los carga en los HashMap de consulta
     */
    public void getAllUsers() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<User> users = userDAO.getAllUsers();
                for (User user: users) {
                    usersInDBByUsername.put(user.getUsername(), user);
                    usersInDBByEmail.put(user.getEmail(), user);
                }
            }
        });
    }

    /**
     * Retorna el HashMap de consulta que utiliza como clave de mapeo el nombre del usuario.
     * @return HashMap de usuarios por clave de nombre de usuario.
     */
    public HashMap<String, User> getUsersByUsername() {
        return usersInDBByUsername;
    }

    /**
     * Retorna el HashMap de consulta que utiliza como clave de mapeo el email del usuario.
     * @return HashMap de usuarios por clave de email.
     */
    public HashMap<String, User> getUsersByEmail() {
        return usersInDBByEmail;
    }

    /**
     * Da de alta un nuevo usuario en el sistema. Para ello, actualiza los HashMap de consulta e inserta la información del usuario en la BD.
     * @param user Usuario a insertar en el sistema.
     */
    public void registerNewUser(User user) {
        usersInDBByUsername.put(user.getUsername(), user);
        usersInDBByEmail.put(user.getEmail(), user);
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDAO.insertUser(user);
            }
        });
    }

    /**
     * Elimina de los HashMap de consulta y de la BD el usuario cuyo nombre es pasado por parámetro.
     * @param username Nombre del usuario que se desea eliminar
     */
    public void deleteUser(String username) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                User user = userDAO.getUser(username);
                usersInDBByUsername.remove(username);
                usersInDBByEmail.remove(user.getEmail());
                userDAO.deleteUserByID(username);
            }
        });
    }

    /**
     * Recupera toda la información del usuario cuyo nombre sea pasado como parámetro.
     * @param username Nombre del usuario del que se quiere recuperar la información
     * @return Usuario cuya clave en el HashMap corresponda con el nombre de usuario pasado como parámetro.
     */
    public User getUser(String username) {
        return usersInDBByUsername.get(username);
    }
}
