import React from "react";
import {useForm} from "react-hook-form";
import HtmlForm from "../../Forms/HtmlForm";
import FormFields from "./FormFields";
import {Alert, Modal} from "react-bootstrap";
import useAxios from "../../../hooks/useAxios";

export default function AddAttribute({ show, setMode, templateId, setAttributeList }) {
    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            priority: 20,
            type: "range",
        }
    });

    const [template, error, loading, axiosFetch] = useAxios((response) => {
        methods.reset();
        setAttributeList(response.data.attributes);
    });

    const onSubmit = (data) => {
        axiosFetch({
            url: `/templates/${templateId}/attributes`,
            method: 'put',
            data: data,
        }).then( res => res && setMode(''));
    }

    return (
        <>
            <Modal centered show={show}>
                <Modal.Header closeButton onHide={()=> setMode('')}><Modal.Title>Add Attribute</Modal.Title></Modal.Header>
                <Modal.Body>
                    {!loading && error && <Alert variant="danger">{error}</Alert> }
                    <HtmlForm formProps={methods} fields={FormFields} submitCallback={onSubmit} loading={loading} submitLabel={"Add"}></HtmlForm>
                </Modal.Body>
            </Modal>
        </>
    );
}