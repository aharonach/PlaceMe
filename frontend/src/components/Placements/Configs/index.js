import useFetchRecord from "../../../hooks/useFetchRecord";
import {useForm} from "react-hook-form";
import {setFormValues} from "../../../utils";
import HtmlForm from "../../Forms/HtmlForm";
import {Alert, Button} from "react-bootstrap";
import FormFields from "./FormFields";
import {useState} from "react";
import axios from "axios";

export default function Configs() {
    const form = useForm();
    const url = '/placements/configs';
    // eslint-disable-next-line no-unused-vars
    const [configs, error, loading, fetch, getConfigs] = useFetchRecord({
        fetchUrl: url,
        thenCallback: (configs) => setFormValues(form, configs)
    });
    const [updated, setUpdated] = useState('');

    const handleSubmit = async (data) => {
        setUpdated('');

        const response = await fetch({
            method: 'post',
            url: url,
            data: data
        });

        if ( response ) {
            setUpdated("Configs updated successfully");
        }
    }

    const handleReset = async () => {
        setUpdated('');

        const response = await fetch({
            method: 'post',
            url: `${url}/reset`,
        });

        if ( response ) {
            setUpdated("Configs reset successfully");
            getConfigs();
        }
    }

    return (
        <>
            <h1>Configure Placing Algorithm</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            {updated && <Alert variant="success">{updated}</Alert>}
            <HtmlForm
                formProps={form}
                fields={FormFields()}
                loading={loading}
                submitCallback={handleSubmit}
                submitLabel={"Update"}
                additionalButtons={<Button type="button" variant="secondary" onClick={handleReset}>Reset to default</Button>}
                rows={2}
            />
        </>
    )
}