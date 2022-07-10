
package com.example.saassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class ItemListActivity extends AppCompatActivity {

    Spinner spnMonth, spnOption;
    ListView lvProduct;
    Button btnAdd, btnUpdate;
    EditText etName, etExpiry, etIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        spnMonth = findViewById(R.id.spinnerMonths);
        spnOption = findViewById(R.id.spinnerOption);
        lvProduct = findViewById(R.id.listViewProduct);
        btnAdd = findViewById(R.id.buttonAdd);
        btnUpdate= findViewById(R.id.buttonUpdate);
        etName = findViewById(R.id.editTextProduct);
        etExpiry = findViewById(R.id.editTextExpiry);
        etIndex = findViewById(R.id.editTextIndex);

        ArrayList<String> alProducts = new ArrayList<String>();
        alProducts.add("Expires 2022-9-7 Macbook");
        alProducts.add("Expires 2021-8-5 Asus Laptop");
        alProducts.add("Expires 2021-10-6 Lenovo Keyboard");
        alProducts.add("Expires 2021-12-4 LG Monitor");
        alProducts.add("Expires 2022-4-7 Samsung Smart TV");

        ArrayAdapter<String> aaProduct = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alProducts);
        lvProduct.setAdapter(aaProduct);

        ArrayList<String> copyList = new ArrayList<String>();
        copyList.addAll(alProducts); //Separate duplicated ArrayList of alProducts


        spnMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sort(copyList);
                alProducts.clear();
                alProducts.addAll(copyList); //Reset alProducts
                switch(position) {
                    case 0:
                        break;
                    case 1:
                        filteredList(alProducts, 1);
                        break;
                    case 2:
                        filteredList(alProducts, 3);
                        break;
                    case 3:
                        filteredList(alProducts, 6);
                        break;
                }
                aaProduct.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        btnAdd.setEnabled(true);
                        btnUpdate.setEnabled(false);
                        etIndex.setEnabled(false);
                        etIndex.setVisibility(View.GONE); //Removes EditTextIndex from visibility in the app
                        etName.setHint(R.string.add_new_product);
                        etExpiry.setHint(R.string.enter_the_products_warranty_expiry_date);
                        break;
                    case 1:
                        btnAdd.setEnabled(false);
                        btnUpdate.setEnabled(true);
                        etIndex.setEnabled(true);
                        etIndex.setVisibility(View.VISIBLE);
                        etName.setHint(R.string.enter_updated_name_of_product);
                        etExpiry.setHint(R.string.updated_warranty_expiry_date);
                        etIndex.setHint(R.string.index_of_updated_product);
                        break;
                }
                aaProduct.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elementName = etName.getText().toString();
                String expiry = etExpiry.getText().toString();
                if (!elementName.isEmpty() && !expiry.isEmpty()) { //If user enters all fields
                    alProducts.add("Expires " + expiry + " " + elementName);
                    copyList.add("Expires " + expiry + " " + elementName);
                    sort(alProducts);
                    sort(copyList);
                }
                else { //If user does not enter one or both fields
                    String msg = "Missing field(s)";
                    Toast.makeText(ItemListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                aaProduct.notifyDataSetChanged();
            }

        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elementName = etName.getText().toString();
                String expiry = etExpiry.getText().toString();
                int index = Integer.parseInt(etIndex.getText().toString());
                if (index >= alProducts.size()) { //If user enters an index out of the range
                    String msg = "Index out of range";
                    Toast.makeText(ItemListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                else if (!elementName.isEmpty() && !expiry.isEmpty() && !etIndex.getText().toString().isEmpty()) { //User enters all fields
                    copyList.set(copyList.indexOf(alProducts.get(index)), "Expires " + expiry + " " + elementName); //Get product String from alProducts and find index of that product String in copyList. After that, set the index found and replace it with new String
                    alProducts.set(index, "Expires " + expiry + " " + elementName);
                    sort(alProducts);
                    sort(copyList);
                }
                else { //User does not enter one or both fields
                    String msg = "Missing field(s)";
                    Toast.makeText(ItemListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                aaProduct.notifyDataSetChanged();
            }
        });

        lvProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                copyList.remove(alProducts.get(position)); //Remove product from copyList through alProducts index pos of product
                alProducts.remove(position); //Must be done after line 171 because alProducts.get(position) will be empty, making it not possible to remove the product from copyList
                aaProduct.notifyDataSetChanged();
                return false;
            }
        });
    }
    public void filteredList(ArrayList<String> alProducts, int numMonths) {
        int c = Calendar.getInstance().get(Calendar.MONTH)+1; //Get current month. +1 starts from 0
        int y = Calendar.getInstance().get(Calendar.YEAR); //Get current year
        if (c + numMonths > 12) { //If the months exceed 12 (this year), it will add current year by 1
            y = y + 1;
        }
        c = (c + numMonths) % 12; //If the months exceed 12 (this year), changes the month to the month of the next year
        int num = 0;
        int count = alProducts.size();
        for (int i = 0; i < count; i++){
            String date1 = alProducts.get(i-num).split(" ")[1]; //As ArrayList is dynamic, num keeps track of the number of times a product is removed from the alProducts. Thus, i - num is done to get the correct index.
            String month = date1.split("-")[1]; //Gets the month of the product
            String year = date1.split("-")[0]; //Gets the year of the product
            if ((c != Integer.parseInt(month)) || (y != Integer.parseInt(year))) { //Checks if current month or year is different from product's month or year respectively.
                alProducts.remove(i-num); //Filters out the products which have the wrong dates
                num += 1;
            }
        }
    }

    public void sort(ArrayList<String> arlist) { //Sorts the alProducts and copyList alphabetically
        Collections.sort(arlist, new Comparator<String>() { //Custom Comparator
            public int compare(String  product1, String  product2) {
                if (product1.split(" ")[2].compareToIgnoreCase(product2.split(" ")[2]) > 0) { //Swap the positions of the first product and second product if first product is bigger than second product alphabetically.
                    return 1;
                } else if (product1.split(" ")[2].compareToIgnoreCase(product2.split(" ")[2]) < 0) { //Opposite of line 187
                    return -1;
                }
                return 0; //If the product names are exactly the same
            }
        });
    }
}