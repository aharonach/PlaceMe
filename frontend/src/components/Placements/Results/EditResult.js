import {useNavigate, useOutletContext, useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import HtmlForm from "../../Forms/HtmlForm";
import FormFields from "./FormFields";
import {Alert} from "react-bootstrap";
import React from "react";

export default function EditResult() {
    const { placementId } = useParams();
    const { result, error, loading, axiosFetch } = useOutletContext();
    const navigate = useNavigate();
    let methods = useForm({
        defaultValues: {
            name: result.name,
            description: result.description
        }
    });

    const editResult = (data) => {
        axiosFetch({
            method: 'post',
            url: `/placements/${placementId}/results/${result.id}`,
            data: data,
        }).then(result => result && navigate(`/placements/${placementId}/results/${result.id}`, { replace: true }));
    }

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm loading={loading} formProps={methods} fields={FormFields} submitCallback={editResult} submitLabel={"Update"} />
        </>
    )
}