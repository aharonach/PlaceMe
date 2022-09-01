import React from "react";
import {Button, Spinner} from "react-bootstrap";
import useAxios from "../../../hooks/useAxios";
import Api from "../../../api";

export default function DeleteAttribute({ templateId, attributeId, attributeList, setAttributeList }) {
    const [response, error, loading, axiosFetch] = useAxios(() => {
        setAttributeList(attributeList.filter( attribute => attribute.id !== attributeId ) );
    });

    const handleDelete = (attributeId) => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/templates/${templateId}/attributes/${attributeId}`,
        });
    };

    return (
        <Button variant="danger" size="sm" onClick={() => handleDelete(attributeId)}>
            {loading && <Spinner
                as="span"
                animation="border"
                role="status"
                size="sm"
                aria-hidden="true"
            />}
            Delete
        </Button>
    )
}