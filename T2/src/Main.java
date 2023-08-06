import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Main {
    private static String DB_NAME = "database_t2";
    private static String DB_URL = "jdbc:mysql://localhost:3306/";
    private static String USER_NAME = "root";
    private static String PASSWORD = "dan123456789.";
    static Scanner scanner = new Scanner(System.in);
    static Connection connection;
    static DefaultTableModel tableModel;
    static int id;

    public static Connection getConnection() throws Throwable {
        try {
            Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, USER_NAME, PASSWORD);
            return connection;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kết nối không thành công \n" + e.toString(), "Thông báo", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static class InputPanel extends JFrame{
        JTextField idTextField, nameTextField, ageTextField, addressTextField, emailTextField;
        JButton saveButton, closeButton;
        int choice;

        InputPanel(int choice) {
            JPanel inputPanel = new JPanel(new GridLayout(5, 2));
            idTextField = new JTextField();
            nameTextField = new JTextField();
            ageTextField = new JTextField();
            addressTextField = new JTextField();
            emailTextField = new JTextField();
            saveButton = new JButton("Lưu");
            closeButton = new JButton("Hủy");

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (choice == 1) {
                        createNewUser(connection, getUser());
                        dispose();
                    } else{
                        updateUser(connection, id, getUser());
                        dispose();
                    }
                }
            });
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            inputPanel.add(new JLabel("Họ tên:"));
            inputPanel.add(nameTextField);
            inputPanel.add(new JLabel("Tuổi:"));
            inputPanel.add(ageTextField);
            inputPanel.add(new JLabel("Quê quán:"));
            inputPanel.add(addressTextField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailTextField);
            inputPanel.add(saveButton);
            inputPanel.add(closeButton);
            
            setTitle("Lập trình ứng dụng với Java");
            setSize(600, 300);
            setLocation(660, 390);    
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            add(inputPanel);
            setVisible(false);
            if (choice == 0) {
                read1User(connection, this);
            }
        }

        public Users getUser() {
            String name = nameTextField.getText();
            int age = Integer.parseInt(ageTextField.getText());
            String address = addressTextField.getText();
            String email = emailTextField.getText();
            return new Users(name, age, address, email);
        }
    }

    public static void main(String[] args) {
        try {
            connection = getConnection();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.setTitle("Lập trình ứng dụng với Java");
        frame.setSize(600, 225);
        frame.setLocation(660, 427);    
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Họ tên");
        tableModel.addColumn("Tuổi");
        tableModel.addColumn("Quê quán");
        tableModel.addColumn("Email");
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Thêm"); 
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputPanel temp = new InputPanel(1);
                temp.setVisible(true);
            }
        });
        JButton updateButton = new JButton("Sửa");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputPanel temp1 = new InputPanel(0);
                temp1.setVisible(true);
            }
        });
        JButton deleteButton = new JButton("Xóa");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser(connection);
            }
        });
        

        JTextField searchTextField = new JTextField(20); // Số 20 xác định số kí tự hiển thị
        JButton searchButton = new JButton("Tìm kiếm");
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Tìm kiếm theo tên:"));
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);

searchButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String searchTerm = searchTextField.getText();
        searchUser(connection, searchTerm);
    }
});

JButton saveToFileButton = new JButton("Lưu File");

saveToFileButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        saveToFile(frame);
    }
});


        JPanel buttonPanel = new JPanel(new GridLayout(0, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveToFileButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.add(searchPanel, BorderLayout.NORTH);
        readUser(connection);

        
    }

    public static void createNewUser(Connection connection, Users newUser) {
        try {
            String query = "INSERT INTO users (name, age, address, email) VALUES(?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newUser.getName());
                statement.setInt(2, newUser.getAge());
                statement.setString(3, newUser.getaddress());
                statement.setString(4, newUser.getEmail());

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Thêm người dùng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Thêm người dùng không thành công", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
        }
        readUser(connection);
    }

    public static void deleteUser(Connection connection) {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Nhập số ID người dùng cần xóa"));
        try {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Xóa người dùng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng có ID tương ứng", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
        }
        readUser(connection);
    }

    public static void readUser(Connection connection) {
        List<Users> users = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");

                Users user = new Users(id, name, age, address, email);
                users.add(user);
            }
            tableModel.setRowCount(0); // Clear previous data
            for (Users user : users) {
                Object[] rowData = {user.getId(), user.getName(), user.getAge(), user.getaddress(), user.getEmail()};
                tableModel.addRow(rowData); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void read1User(Connection connectionn, InputPanel inputPanel) {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Nhập vào ID của người dùng cần chỉnh sửa:"));
        Main.id = id;
        String name, address, email;
        int age;

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users where id = " + id + ";";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                name = resultSet.getString("name");
                age = resultSet.getInt("age");
                address = resultSet.getString("address");
                email = resultSet.getString("email");

                inputPanel.nameTextField.setText(name);
                inputPanel.ageTextField.setText(Integer.toString(age));
                inputPanel.addressTextField.setText(address);
                inputPanel.emailTextField.setText(email);
                break;
            }
        }  catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    public static void updateUser(Connection connection, int id, Users editUser) {
        try {
            String query2 = "UPDATE users SET name = ?, age = ?, address = ?, email = ? WHERE id = ?";
            try (PreparedStatement statement2 = connection.prepareStatement(query2)) {
                statement2.setString(1, editUser.getName());
                statement2.setInt(2, editUser.getAge());
                statement2.setString(3, editUser.getaddress());
                statement2.setString(4, editUser.getEmail());
                statement2.setInt(5, id);

                int rowsUpdated = statement2.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Cập nhật người dùng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng có ID tương ứng", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
        }
        readUser(connection);
    }

    // Thêm hàm thực hiện tìm kiếm và cập nhật bảng
public static void searchUser(Connection connection, String searchTerm) {
    List<Users> users = new ArrayList<>();
    
    try {
        String query = "SELECT * FROM users WHERE name LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");
                
                Users user = new Users(id, name, age, address, email);
                users.add(user);
            }
            
            tableModel.setRowCount(0); // Clear previous data
            for (Users user : users) {
                Object[] rowData = {user.getId(), user.getName(), user.getAge(), user.getaddress(), user.getEmail()};
                tableModel.addRow(rowData);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static void saveToFile(JFrame frame) {
    JFileChooser fileChooser = new JFileChooser();
    int option = fileChooser.showSaveDialog(frame);
    
    if (option == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    writer.print(tableModel.getValueAt(row, col));
                    if (col < tableModel.getColumnCount() - 1) {
                        writer.print(", ");
                    }
                }
                writer.println(); // Xuống dòng sau mỗi dòng dữ liệu
            }
            writer.close();
            JOptionPane.showMessageDialog(frame, "Dữ liệu đã được lưu vào tập tin thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Lỗi khi lưu tập tin: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

}