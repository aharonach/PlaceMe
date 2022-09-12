import React from 'react';
import {useParams, useNavigate, Outlet} from "react-router-dom";
import Loading from "../Loading";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
import useFetchRecord from "../../hooks/useFetchRecord";
import {LinkContainer} from 'react-router-bootstrap';

export default function TemplatePage() {
    let { templateId } = useParams();
    const [template, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: `/templates/${templateId}`,
        displayFields: ['name']
    });

    const navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: `/templates/${templateId}`,
        }).then(() => navigate('/templates', { replace: true }));
    }

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && template &&
                <article className="template">
                    <div className={"page-header"}>
                        <h1>{template.name}</h1>
                        <ButtonGroup>
                            <LinkContainer to={`/templates/${template.id}/edit`}><Button>Edit</Button></LinkContainer>
                            <Button variant="danger" onClick={handleDelete}>Delete Template</Button>
                        </ButtonGroup>
                    </div>
                    <Outlet context={{ template, error, loading, axiosFetch }} />
                </article>
            }
        </>
    )
}
