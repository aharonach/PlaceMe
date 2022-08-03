import React, {useState} from 'react';
import TableList from "../TableList";
import AddAttribute from "../Attributes/AddAttribute";
import DeleteAttribute from "../Attributes/DeleteAttribute";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";

export default function Attributes({ template }) {
    const [attributeList, setAttributeList] = useState(template.attributes);
    const [response, error, loading, axiosFetch] = useAxios();

    const handleDelete = (attributeId) => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/templates/${template.id}/attributes/${attributeId}`,
        });
        // attributeList.filter( attribute => attribute.id !== attributeId);
        // setAttributeList(setAttributeList);
        // setAttributeList(response.attributes);
    }

    const columns = {
        id: "ID",
        name: "Name",
        description: "Description",
        type: "Type",
        priority: "Priority",
        createdTime: "Created Time",
        actions: {
            label: "",
            callbacks: [
                (attribute) => <DeleteAttribute key={attribute.id} handleDelete={handleDelete} attributeId={attribute.id} />
            ]
        }
    };

    return (
        <>
            <h2>Attributes</h2>
            <AddAttribute templateId={template.id} setAttributeList={setAttributeList} />
            <TableList basePath={`/templates/${template.id}/attributes`} columns={columns} items={attributeList} />
        </>
    );
}