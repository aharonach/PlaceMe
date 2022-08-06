import React, {useEffect} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditTemplate from "./EditTemplate";
import Attributes from "./Attributes";

export default function TemplatePage() {
    let { templateId } = useParams();
    const [template, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();

    const getGroup = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/templates/${templateId}`,
        });
    }

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/templates/${templateId}`,
        }).then(() => navigate('/templates', { replace: true }));
    }

    useEffect(() => {
        getGroup();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && template &&
                <article className="template">
                    <h2>{template.name} (ID: {template.id})</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete Template</Button>
                    <EditTemplate template={template} />
                    <Attributes template={template} />
                </article>
            }
        </>
    )
}
