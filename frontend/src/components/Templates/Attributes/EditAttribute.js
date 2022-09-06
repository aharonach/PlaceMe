import React from "react";
import {useForm} from "react-hook-form";
import HtmlForm from "../../Forms/HtmlForm";
import FormFields from "./FormFields";
import {Alert, Modal} from "react-bootstrap";
import useAxios from "../../../hooks/useAxios";
import {getDefaultValuesByFields} from "../../../utils";

export default function EditAttribute({ templateId, attribute, setAttribute, setAttributeList }) {
    let methods = useForm({
        defaultValues: getDefaultValuesByFields( FormFields(), attribute ),
    });

    const [response, error, loading, axiosFetch] = useAxios((response) => {
        methods.reset();
        setAttributeList(response.data.attributes);
        setAttribute(null);
    });

    const onHide = () => {
        setAttribute(null);
    };

    const onSubmit = (data) => {
        axiosFetch({
            url: `/templates/${templateId}/attributes/${attribute.id}`,
            method: 'post',
            data: { id: attribute.id, ...data },
        });
    }

    return (
        <>
            <Modal centered show={!!attribute}>
                <Modal.Header closeButton onHide={onHide}><Modal.Title>Edit Attribute</Modal.Title></Modal.Header>
                <Modal.Body>
                    {!loading && error && <Alert variant="danger">{error}</Alert> }
                    <HtmlForm
                        formProps={methods}
                        fields={FormFields}
                        submitCallback={onSubmit}
                        loading={loading}
                        submitLabel="Update"
                    />
                </Modal.Body>
            </Modal>
        </>
    );
}